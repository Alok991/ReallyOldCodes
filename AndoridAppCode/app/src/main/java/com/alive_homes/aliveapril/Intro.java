package com.alive_homes.aliveapril;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class Intro extends AppIntro {


    @Override
    public void init(@Nullable Bundle savedInstanceState) {
        addSlide(AppIntroFragment.newInstance("A", "abcd", R.drawable.comfort, Color.GREEN));
        addSlide(AppIntroFragment.newInstance("B", "abcd", R.drawable.logo, Color.WHITE));
        addSlide(AppIntroFragment.newInstance("C", "abcd", R.drawable.logo, Color.YELLOW));
        addSlide(AppIntroFragment.newInstance("D", "abcd", R.drawable.logo, Color.RED));

        setSlideOverAnimation();

    }

    @Override
    public void onSkipPressed() {
        goToMainActivity();
    }

    @Override
    public void onNextPressed() {

    }

    @Override
    public void onDonePressed() {
        goToMainActivity();
    }

    @Override
    public void onSlideChanged() {

    }

    private void goToMainActivity() {
        Intent i= new Intent(Intro.this,MainActivity.class);
        startActivity(i);
    }
}
