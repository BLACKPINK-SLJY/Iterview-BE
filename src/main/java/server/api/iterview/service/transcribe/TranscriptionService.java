package server.api.iterview.service.transcribe;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.transcribe.AmazonTranscribe;
import com.amazonaws.services.transcribe.AmazonTranscribeClientBuilder;
import com.amazonaws.services.transcribe.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.api.iterview.domain.member.Member;
import server.api.iterview.dto.transcription.TranscriptionResponseDTO;
import server.api.iterview.repository.MemberRepository;
import server.api.iterview.response.BizException;
import server.api.iterview.response.transcribe.TranscribeResponseType;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TranscriptionService {
    private final AmazonS3Client s3Client;

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.S3.bucket}")
    private String bucket;

    private final MemberRepository memberRepository;

    public AmazonTranscribe transcribeClient(){
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);

        return AmazonTranscribeClientBuilder.standard().withCredentials(awsStaticCredentialsProvider).withRegion(region).build();
    }

    /* **** Start Transcription Job Method******/
    public StartTranscriptionJobResult startTranscriptionJob(String key){
        Media media = new Media().withMediaFileUri(s3Client.getUrl(bucket, key).toExternalForm());
        String jobName = UUID.randomUUID().toString();
        StartTranscriptionJobRequest startTranscriptionJobRequest = new StartTranscriptionJobRequest()
                .withLanguageCode(LanguageCode.KoKR).withTranscriptionJobName(jobName).withMedia(media);
        StartTranscriptionJobResult startTranscriptionJobResult = transcribeClient()
                .startTranscriptionJob(startTranscriptionJobRequest);

        return startTranscriptionJobResult;
    }

    /* *** Get Transcription Job Result method *********/
    public GetTranscriptionJobResult getTranscriptionJobResult(String jobName){
        log.info("Get Transcription Job Result By Job Name : {}", jobName);
        GetTranscriptionJobRequest getTranscriptionJobRequest = new GetTranscriptionJobRequest()
                .withTranscriptionJobName(jobName);
        Boolean resultFound = false;
        GetTranscriptionJobResult getTranscriptionJobResult = new GetTranscriptionJobResult();
        TranscriptionJob transcriptionJob;
        int i = 0;

        while(!resultFound){
            getTranscriptionJobResult = transcribeClient().getTranscriptionJob(getTranscriptionJobRequest);
            transcriptionJob = getTranscriptionJobResult.getTranscriptionJob();
            if(transcriptionJob.getTranscriptionJobStatus()
                    .equalsIgnoreCase(TranscriptionJobStatus.COMPLETED.name())){
                return getTranscriptionJobResult;
            }
            else if(transcriptionJob.getTranscriptionJobStatus()
                    .equalsIgnoreCase(TranscriptionJobStatus.FAILED.name())){
                return null;
            }
            else if(transcriptionJob.getTranscriptionJobStatus()
                    .equalsIgnoreCase(TranscriptionJobStatus.IN_PROGRESS.name())){
                try{
                    Thread.sleep(4 * 1000); // 3초
                    System.out.println(i++ + "===============");
                    System.out.println(transcriptionJob.getStartTime());
                    System.out.println(transcriptionJob.getCompletionTime());
                }catch (InterruptedException e){
                    throw new BizException(TranscribeResponseType.TRANSCRIBE_FAIL);
                }
            }
        }

        return getTranscriptionJobResult;
    }

    /*  Download Transcription Result from URI Method *********/
    public TranscriptionResponseDTO downloadTranscriptionResponse(String uri){
        OkHttpClient okHttpClient = new OkHttpClient()
                .newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder().url(uri).build();
        Response response;
        try{
            response = okHttpClient.newCall(request).execute();
            String body = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            response.close();
            return objectMapper.readValue(body, TranscriptionResponseDTO.class);

        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /* **** Delete Transcription Job Method ************
     * TO delete transcription job after getting result****/
    public void deleteTranscriptionJob(String jobName){
        System.out.println("Delete Transcription Job from amazon Transcribe : " + jobName);
        DeleteTranscriptionJobRequest deleteTranscriptionJobRequest = new DeleteTranscriptionJobRequest()
                .withTranscriptionJobName(jobName);
        transcribeClient().deleteTranscriptionJob(deleteTranscriptionJobRequest);
    }

    public TranscriptionResponseDTO extractSpeechTextFromVideo(Member member, Long questionId) {
        String username = member.getAccount();
        String fileName = null;

        log.info("Request to extract Speech Text from Video : " + username + "/" + questionId + "/" + fileName);

        // Start Transcription Job and get result
        StartTranscriptionJobResult startTranscriptionJobResult = startTranscriptionJob(username + "/" + fileName);

        // Get name of job started for the file
        String transcriptionJobName = startTranscriptionJobResult.getTranscriptionJob().getTranscriptionJobName();

        // Get result after the processing is complete
        GetTranscriptionJobResult getTranscriptionJobResult = getTranscriptionJobResult(transcriptionJobName);

        // Url of result file for transcription
        String transcriptFileUriString = getTranscriptionJobResult.getTranscriptionJob().getTranscript().getTranscriptFileUri();

        // Get the transcription response by downloading the file
        TranscriptionResponseDTO transcriptionResponseDTO = downloadTranscriptionResponse(transcriptFileUriString);

        //Delete the transcription job after finishing, or it will get deleted after 90 days automatically if you do not call
        deleteTranscriptionJob(transcriptionJobName);

        return transcriptionResponseDTO;
    }

}
