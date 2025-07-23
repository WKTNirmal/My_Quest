package com.wktnirmal.myquest;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Quest {
    private String id;
    private String QuestTitle;
    private String QuestDescription;
    private double StartLat;
    private double StartLng;
    private double EndLat;
    private double EndLng;
    private int Distance;
    private String QuestStatus;
    private String Repetitive;

    public Quest() {} //needed for Firebase
    public Quest(String questTitle, String questDescription, double startLat, double startLng, double endLat, double endLng, int distance, String questStatus, String repetitive) {
        //id = id;
        QuestTitle = questTitle;
        QuestDescription = questDescription;
        StartLat = startLat;
        StartLng = startLng;
        EndLat = endLat;
        EndLng = endLng;
        Distance = distance;
        QuestStatus = questStatus;
        Repetitive = repetitive;
    }


    public String getRepetitive() {
        return Repetitive;
    }

    public String getQuestStatus() {
        return QuestStatus;
    }

    public int getDistance() {
        return Distance;
    }

    public double getEndLng() {
        return EndLng;
    }

    public double getEndLat() {
        return EndLat;
    }

    public double getStartLng() {
        return StartLng;
    }

    public double getStartLat() {
        return StartLat;
    }

    public String getQuestDescription() {
        return QuestDescription;
    }

    public String getQuestTitle() {
        return QuestTitle;
    }

    public String getId() {
        return id;
    }
}
