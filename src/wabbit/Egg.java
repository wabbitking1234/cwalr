package wabbit;

import wabbit.DrawFlyersAdvice;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Instant;
import arc.*;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.*;
import arc.func.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.type.*;
import mindustry.ui.fragments.MenuFragment;
import mindustry.world.*;
import mindustry.content.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.mod.*;
import mindustry.ui.dialogs.*;
import mindustry.graphics.*;


import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.Argument;
import net.bytebuddy.implementation.bind.annotation.FieldValue;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;



public class Egg extends Mod {


    public Egg() {
        Log.info("Loaded !!egg!! Constructor.");
        Events.on(ClientLoadEvent.class, e -> {
            Log.info("Hello, do I exist?");


            try {
                MenuFragment menuFragment = Vars.ui.menufrag;
                if(menuFragment == null){
                    Log.err("MenuFragment is null!");
                    return;
                }

                Field rendererField = MenuFragment.class.getDeclaredField("renderer");
                rendererField.setAccessible(true);
                MenuRenderer renderer = (MenuRenderer) rendererField.get(menuFragment);
                rendererField.setAccessible(false);
                Log.info("Menu Time!");
                if(renderer == null){
                    Log.err("MenuRenderer is null!");
                    return;
                }

                Field flyerTypeField = MenuRenderer.class.getDeclaredField("flyerType");
                flyerTypeField.setAccessible(true);
                flyerTypeField.set(renderer, UnitTypes.crawler);
                flyerTypeField.setAccessible(false);
                Log.info("cwalr");

                //Tried to make them spin, failed miserably.

                ByteBuddyAgent.install();
                new ByteBuddy()
                        .redefine(MenuRenderer.class)
                        .visit(Advice.to(FlyersAdvice.class)
                                .on(named("flyers").and(takesArguments(Floatc2.class))))
                        .make()
                        .load(MenuRenderer.class.getClassLoader(),
                                ClassReloadingStrategy.fromInstalledAgent());

                Log.info("AHHHHHHH");

                //new ByteBuddy()
                //        .redefine(MenuRenderer.class)
                //        .visit(Advice.to(DrawFlyersAdvice.class)
                //                .on(named("drawFlyers")))
                //        .make()
                //        .load(MenuRenderer.class.getClassLoader(),
                //                ClassReloadingStrategy.fromInstalledAgent());
                //Log.info("MenuRenderer modified successfully!");



            } catch(Exception ex){
                ex.printStackTrace();
            }
        });











    }
}

class FlyersAdvice {
    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static boolean enter(@Advice.Argument(0) Floatc2 cons) {

        try {
            MenuFragment menuFragment = Vars.ui.menufrag;
            Field rendererField = MenuFragment.class.getDeclaredField("renderer");
            rendererField.setAccessible(true);
            MenuRenderer renderer = (MenuRenderer) rendererField.get(menuFragment);
            rendererField.setAccessible(false);

            Field timeField = renderer.getClass().getDeclaredField("time");
            timeField.setAccessible(true);
            float time = timeField.getFloat(renderer);
            rendererField.setAccessible(false);

            Field flyerRotField = renderer.getClass().getDeclaredField("flyerRot");
            flyerRotField.setAccessible(true);
            float flyerRot = flyerRotField.getFloat(renderer);
            rendererField.setAccessible(false);

            Field flyersField = renderer.getClass().getDeclaredField("flyers");
            flyersField.setAccessible(true);
            int flyers = flyersField.getInt(renderer);
            rendererField.setAccessible(false);

            Field flyerTypeField = renderer.getClass().getDeclaredField("flyerType");
            flyerTypeField.setAccessible(true);
            UnitType flyerType = (UnitType) flyerTypeField.get(renderer);
            rendererField.setAccessible(false);

            //(I'll figure out how to reflect these later. ByteBuddy is for desktop only, so these should be fine for now.)
            int width = 100;
            int height = 50;
            float tilesize = 8f;

            float tw = width * tilesize + tilesize;
            float th = height * tilesize + tilesize;
            float range = 500f;
            float offset = -100f;
            float roteee = (float)((Instant.now().getNano() * 0.00000036) % 360);

            for (int i = 0; i < flyers; i++) {
                Tmp.v1.trns(flyerRot, time * flyerType.speed);
                flyerType.sample.rotation = roteee;
                cons.get(
                        (Mathf.randomSeedRange(i, range) + Tmp.v1.x + Mathf.absin(time + Mathf.randomSeedRange(i + 2, 500), 10f, 3.4f) + offset) % (tw + Mathf.randomSeed(i + 5, 0, 500)),
                        (Mathf.randomSeedRange(i + 1, range) + Tmp.v1.y + Mathf.absin(time + Mathf.randomSeedRange(i + 3, 500), 10f, 3.4f) + offset) % th
                );
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }
}








