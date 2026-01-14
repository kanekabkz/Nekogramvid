/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Color;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.CueGroup;
import com.google.android.exoplayer2.text.TextOutput;

import org.telegram.messenger.AndroidUtilities;

import java.util.ArrayList;
import java.util.List;

public class VideoSubtitleView extends TextView implements TextOutput {

    private final SpannableStringBuilder cueText = new SpannableStringBuilder();

    public VideoSubtitleView(Context context) {
        super(context);
        setGravity(Gravity.CENTER);
        setTextColor(Color.WHITE);
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        setShadowLayer(AndroidUtilities.dp(2), 0, 0, 0x80000000);
        setMaxLines(6);
        setVisibility(GONE);
    }

    @Override
    public void onCues(List<Cue> cues) {
        updateCues(cues);
    }

    @Override
    public void onCues(CueGroup cueGroup) {
        updateCues(cueGroup.cues);
    }

    private void updateCues(List<Cue> cues) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            List<Cue> safeCues = new ArrayList<>(cues);
            AndroidUtilities.runOnUIThread(() -> applyCues(safeCues));
            return;
        }
        applyCues(cues);
    }

    private void applyCues(List<Cue> cues) {
        cueText.clear();
        cueText.clearSpans();
        boolean hasText = false;
        for (int i = 0; i < cues.size(); i++) {
            CharSequence text = cues.get(i).text;
            if (TextUtils.isEmpty(text)) {
                continue;
            }
            if (cueText.length() > 0) {
                cueText.append('\n');
            }
            cueText.append(text);
            hasText = true;
        }
        if (!hasText) {
            setText(null);
            setVisibility(GONE);
        } else {
            setText(cueText);
            setVisibility(VISIBLE);
        }
    }
}
