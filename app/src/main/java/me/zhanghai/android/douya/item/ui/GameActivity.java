/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import me.zhanghai.android.douya.network.api.info.frodo.Game;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleGame;
import me.zhanghai.android.douya.util.FragmentUtils;

public class GameActivity extends AppCompatActivity {

    private static final String KEY_PREFIX = GameActivity.class.getName() + '.';

    private static final String EXTRA_GAME_ID = KEY_PREFIX + "game_id";
    private static final String EXTRA_SIMPLE_GAME = KEY_PREFIX + "simple_game";
    private static final String EXTRA_GAME = KEY_PREFIX + "game";

    public static Intent makeIntent(long gameId, Context context) {
        return new Intent(context, GameActivity.class)
                .putExtra(EXTRA_GAME_ID, gameId);
    }

    public static Intent makeIntent(SimpleGame simpleGame, Context context) {
        return makeIntent(simpleGame.id, context)
                .putExtra(EXTRA_SIMPLE_GAME, simpleGame);
    }

    public static Intent makeIntent(Game game, Context context) {
        return makeIntent((SimpleGame) game, context)
                .putExtra(EXTRA_GAME, game);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Calls ensureSubDecor().
        findViewById(android.R.id.content);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            long gameId = intent.getLongExtra(EXTRA_GAME_ID, -1);
            SimpleGame simpleGame = intent.getParcelableExtra(EXTRA_SIMPLE_GAME);
            Game game = intent.getParcelableExtra(EXTRA_GAME);
            FragmentUtils.add(GameFragment.newInstance(gameId, simpleGame, game), this,
                    android.R.id.content);
        }
    }
}
