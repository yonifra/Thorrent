package com.cryptocodes.mediator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yonifra on 7/8/17.
 */

class TitleParser {
    private static String notAvailableString = "N/A";

    static String parseTitle(String unparsedText) {
        String parsedSE = parseSeasonAndEpisode(unparsedText);

        if (parsedSE != notAvailableString) {
            int index = unparsedText.indexOf(parsedSE);
            return unparsedText.substring(0, index - 1);
        } else {
            return notAvailableString;
        }

    }

    static String parseQuality(String unparsedText) {
        Pattern pattern = Pattern.compile("(\\d+p)");
        Matcher matcher = pattern.matcher(unparsedText);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return notAvailableString;
        }
    }

    static String parseSeasonAndEpisode(String unparsedText) {
        Pattern pattern = Pattern.compile("(S\\d+E\\d+)");
        Matcher matcher = pattern.matcher(unparsedText);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return notAvailableString;
        }
    }

    public static int parseEpisodeNumber(String seText) {
        if (seText == null || seText.isEmpty())
            return 0;

        int index = seText.indexOf('E');

        if (index > 0) {
            return Integer.parseInt(seText.substring(index + 1, seText.length()));
        }

        return 0;
    }

    public static int parseSeasonNumber(String seText) {
        if (seText == null || seText.isEmpty())
            return 0;

        int index = seText.indexOf('S');

        if (index >= 0) {
            return Integer.parseInt(seText.substring(index + 1, seText.indexOf('E')));
        }

        return 0;
    }
}
