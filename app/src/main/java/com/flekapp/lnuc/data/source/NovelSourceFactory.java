package com.flekapp.lnuc.data.source;

import android.support.annotation.NonNull;

import com.flekapp.lnuc.data.entity.Source;
import com.flekapp.lnuc.data.source.impl.LnmtlNovelSource;
import com.flekapp.lnuc.data.source.impl.RulateNovelSource;

public class NovelSourceFactory {
    public static NovelSource getSource(@NonNull Source source) throws NullPointerException {
        switch (source) {
            case LNMTL:
                return new LnmtlNovelSource();
            case RULATE:
                return new RulateNovelSource();
            default:
                return null;
        }
    }

    public static Source getSourceByUrl(@NonNull String url) throws NullPointerException, IllegalArgumentException {
        NovelSource source;
        source = new LnmtlNovelSource();
        if (url.startsWith(source.getUrl())) {
            return Source.LNMTL;
        }
        source = new RulateNovelSource();
        if (url.startsWith(source.getUrl())) {
            return Source.RULATE;
        }

        return null;
    }
}
