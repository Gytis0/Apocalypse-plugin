package Model;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.NumberConversions;

import java.util.HashMap;
import java.util.Map;

public class DifficultySetting implements ConfigurationSerializable {
    private float base;
    private float scale;
    private float linear;

    public DifficultySetting(){
        this.base = 5;
        this.scale = 1;
        this.linear = 0;
    }

    @Override
    public Map<String, Object> serialize(){
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("base", base);
        serialized.put("scale", scale);
        serialized.put("linear", linear);
        return serialized;
    }

    public void deserialize(Object base, Object linear, Object scale){
        this.base = NumberConversions.toFloat(base);
        this.linear = NumberConversions.toFloat(linear);
        this.scale = NumberConversions.toFloat(scale);
    }

    public void setSettings(Float value, Float scale, Float linear){
        if(value != null){
            this.base = value;
        }
        if(scale !=null){
            this.scale = scale;
        }
        if(linear != null){
            this.linear = linear;
        }
    }

    public float scaleUp(long nightCount){
        nightCount--;
        float ans = base;
        for (int i = 0; i < nightCount; i++){
            ans += linear;
            ans *= scale;
        }
        return ans;
    }

    public float getBase() {return base;}
    public float getLinear() {return linear;}
    public float getScale() {return scale;}
}
