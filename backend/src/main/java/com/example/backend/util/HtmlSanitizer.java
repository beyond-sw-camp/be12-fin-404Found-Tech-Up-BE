package com.example.backend.util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class HtmlSanitizer {
    public static String sanitize(String dirtyHtml) {
        Safelist safelist = Safelist.relaxed()
                .addTags("img")  // img 태그 추가
                .addAttributes("img", "src", "alt", "width", "height", "style");  // img에 필요한 속성 허용

        return Jsoup.clean(dirtyHtml, safelist);
    }
}
