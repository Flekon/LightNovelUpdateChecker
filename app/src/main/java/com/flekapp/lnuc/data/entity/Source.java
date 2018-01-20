package com.flekapp.lnuc.data.entity;

public enum Source {
    LNMTL("LNMTL"), RULATE("RULATE");

    private String name;

    Source(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Source getByName(String name) {
        switch (name) {
            case "LNMTL":
                return Source.LNMTL;
            case "RULATE":
                return Source.RULATE;
        }

        return null;
    }
}
