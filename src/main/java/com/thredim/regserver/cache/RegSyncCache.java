package com.thredim.regserver.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RegSyncCache {
    private static RegSyncCache instance = new RegSyncCache();

    private Map<String, RegAtom> regAtomMap = new ConcurrentHashMap<>();

    private boolean status = false;

    private RegSyncCache(){}

    public static RegSyncCache getInstance(){
        return instance;
    }

    public Map<String, RegAtom> getRegAtomMap() {
        return regAtomMap;
    }

    public RegAtom get(String pollCode){
        while(!status){
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return regAtomMap.get(pollCode);
    }

    public void add(RegAtom regAtom){
        regAtomMap.put(regAtom.getPollCode(), regAtom);
    }

    public void done(){
        status = true;
    }
}
