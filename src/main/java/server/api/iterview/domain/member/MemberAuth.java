package server.api.iterview.domain.member;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public enum MemberAuth {
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN");

    private final String abbreviation;

    private static final Map<String, MemberAuth> lookup = new HashMap<>();

    static{
        for(MemberAuth auth : MemberAuth.values()){
            lookup.put(auth.abbreviation, auth);
        }
    }

    public String getAbbreviation(){
        return this.abbreviation;
    }

    public static MemberAuth get(String abbreviation){
        return lookup.get(abbreviation);
    }

    public static boolean containsKey(String abbreviation){
        return lookup.containsKey(abbreviation);
    }
}

