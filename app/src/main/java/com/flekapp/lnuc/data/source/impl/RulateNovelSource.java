package com.flekapp.lnuc.data.source.impl;

import android.support.annotation.NonNull;

import com.flekapp.lnuc.data.entity.Chapter;
import com.flekapp.lnuc.data.entity.Novel;
import com.flekapp.lnuc.data.source.NovelSource;

import java.util.List;

public class RulateNovelSource implements NovelSource {
    @Override
    public String getName() {
        return "RuLate";
    }

    @Override
    public String getUrl() {
        return "https://tr.rulate.ru/";
    }

    @Override
    public List<Novel> getNovels() {
        return null;
    }

    @Override
    public List<Chapter> getLastChapters(@NonNull Novel novel) {
        return null;
    }
}
