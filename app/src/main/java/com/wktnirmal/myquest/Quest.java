package com.wktnirmal.myquest;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Quest {
    private String id;
    private String questTitle;
    private String questDescription;
    private double startLat;
    private double startLng;
    private double endLat;
    private double endLng;
    private int distance;
    private String questStatus;
    private String repetitive;

    public Quest() {} //needed for Firebase
    public Quest(String QuestTitle, String QuestDescription, double StartLat, double StartLng, double EndLat, double EndLng, int Distance, String QuestStatus, String Repetitive) {
        //id = id;
        questTitle = QuestTitle;
        questDescription = QuestDescription;
        startLat = StartLat;
        startLng = StartLng;
        endLat = EndLat;
        endLng = EndLng;
        distance = Distance;
        questStatus = QuestStatus;
        repetitive = Repetitive;
    }


    public String getRepetitive() {
        return repetitive;
    }

    public String getQuestStatus() {
        return questStatus;
    }

    public int getDistance() {
        return distance;
    }

    public double getEndLng() {
        return endLng;
    }

    public double getEndLat() {
        return endLat;
    }

    public double getStartLng() {
        return startLng;
    }

    public double getStartLat() {
        return startLat;
    }

    public String getQuestDescription() {
        return questDescription;
    }

    public String getQuestTitle() {
        return questTitle;
    }

    public String getId() {
        return id;
    }
}
