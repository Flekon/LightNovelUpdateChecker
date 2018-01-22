package com.flekapp.lnuc.data.source.impl;

import android.support.annotation.NonNull;
import android.util.Log;

import com.flekapp.lnuc.data.entity.Chapter;
import com.flekapp.lnuc.data.entity.Novel;
import com.flekapp.lnuc.data.entity.Source;
import com.flekapp.lnuc.data.source.NovelSource;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class LnmtlNovelSource implements NovelSource {
    private static final String TAG = LnmtlNovelSource.class.getSimpleName();
    private static final String USER_AGENT = "Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/_BuildID_) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36";
    private static final Integer LAST_CHAPTERS_COUNT = 10;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    @Override
    public String getName() {
        return "LNMTL";
    }

    @Override
    public String getUrl() {
        return "https://lnmtl.com/";
    }

    @Override
    public List<Novel> getNovels() {
        List<Novel> novels = new ArrayList<>();

        final String urlTemplate = getUrl().concat("novel?page=");
        int page = 0;
        boolean fetchNextPage = true;

        try {
            while (fetchNextPage) {
                page++;
                String url = urlTemplate.concat(String.valueOf(page));
                Document doc = Jsoup.connect(url)
                        .header("Accept-Encoding", "gzip, deflate")
                        .userAgent(USER_AGENT)
                        .get();
                Elements medias = doc.select(".media");

                if (medias.size() > 0) {
                    for (Element media : medias) {
                        Element image = media.getElementsByClass("media-left").get(0).getElementsByTag("img").get(0);
                        String imageUrl = image.absUrl("src");
                        Element link = media.getElementsByClass("media-title").get(0).getElementsByTag("a").get(0);
                        String name = link.text().trim();
                        String novelUrl = link.absUrl("href");

                        if (!name.isEmpty() && novelUrl != null) {
                            String shortName = "";
                            String[] words = name.split(" ");
                            for (String word : words) {
                                if (word.length() > 0) {
                                    shortName = shortName.concat(String.valueOf(word.charAt(0)).toUpperCase());
                                }
                            }

                            Novel novel = new Novel();
                            novel.setName(name);
                            novel.setShortName(shortName);
                            novel.setUrl(novelUrl);
                            novel.setImageUrl(imageUrl);
                            novel.setSource(Source.LNMTL);

                            novels.add(novel);
                        }
                    }
                } else {
                    fetchNextPage = false;
                }
            }

            return novels;
        } catch (IOException e) {
            Log.e(TAG, "Exception thrown while load list of novels !", e);
        }

        return null;
    }

    @Override
    public List<Chapter> getLastChapters(@NonNull Novel novel) {
        if (novel.getUrl() == null || novel.getUrl().isEmpty()) {
            throw new IllegalArgumentException("The link to the source of the novel is incorrect.");
        }

        List<Chapter> newChapters = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(novel.getUrl())
                    .header("Accept-Encoding", "gzip, deflate")
                    .userAgent(USER_AGENT)
                    .get();
            Elements chapters = doc.select("table tr");

            Iterator<Element> it = chapters.iterator();
            int count = LAST_CHAPTERS_COUNT;
            while (it.hasNext() && count > 0) {
                Element cur = it.next();
                Element link = cur.getElementsByClass("chapter-link").get(0);
                String date = cur.getElementsByClass("label-default").get(0).text();
                String number = link.getElementsByClass("chapter-badge").text();
                String caption = link.text().replace(number, "").trim();
                String status = "";
                String url = link.absUrl("href");

                Chapter chapter = new Chapter();
                chapter.setNovel(novel);
                chapter.setNumber(number);
                chapter.setTitle(caption);
                chapter.setUrl(url);
                chapter.setStatus(status);
                try {
                    chapter.setReleaseDate(DATE_FORMAT.parse(date));
                } catch (ParseException e) {
                    chapter.setReleaseDate(new Date());
                }

                newChapters.add(chapter);

                count--;
            }

            return newChapters;
        } catch (IOException e) {
            Log.e(TAG, "Exception thrown while load last chapters !", e);
        }

        return null;
    }
}
