package com.flekapp.lnuc.data.source;

import android.support.annotation.NonNull;

import com.flekapp.lnuc.data.entity.Chapter;
import com.flekapp.lnuc.data.entity.Novel;

import java.util.List;

public interface NovelSource {
    String getName();
    String getUrl();
    List<Novel> getNovels();
    List<Chapter> getLastChapters(@NonNull Novel novel);
}
