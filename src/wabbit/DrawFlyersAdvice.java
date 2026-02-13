package wabbit;

import arc.func.Floatc2;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.graphics.MenuRenderer;
import mindustry.type.UnitType;
import mindustry.ui.fragments.MenuFragment;
import net.bytebuddy.asm.Advice;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class DrawFlyersAdvice {
    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static boolean enter(){
        try {
            MenuFragment menuFragment = Vars.ui.menufrag;

            Field rendererField = MenuFragment.class.getDeclaredField("renderer");
            rendererField.setAccessible(true);
            MenuRenderer renderer = (MenuRenderer) rendererField.get(menuFragment);
            rendererField.setAccessible(false);

            Field flyerTypeField = renderer.getClass().getDeclaredField("flyerType");
            flyerTypeField.setAccessible(true);
            UnitType flyerType = (UnitType) flyerTypeField.get(renderer);
            rendererField.setAccessible(false);

            Field flyerRotField = renderer.getClass().getDeclaredField("flyerRot");
            flyerRotField.setAccessible(true);
            float flyerRot = flyerRotField.getFloat(renderer);
            rendererField.setAccessible(false);

            Method flyersMethod = renderer.getClass().getDeclaredMethod("flyers", Floatc2.class);
            flyerTypeField.setAccessible(true);

            flyerType.sample.elevation = 1f;
            flyerType.sample.team = Team.sharded;
            flyerType.sample.rotation = flyerRot;
            flyerType.sample.heal();

            flyersMethod.invoke(renderer, (Floatc2)((x, y) -> {
                flyerType.sample.set(x, y);
                flyerType.drawShadow(flyerType.sample);
                flyerType.draw(flyerType.sample);
            }));
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return true;
    }
}
