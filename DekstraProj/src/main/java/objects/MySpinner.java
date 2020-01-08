package objects;

import constants.Constants;
import javafx.scene.control.Spinner;

public class MySpinner<T> extends Spinner<T>
{
    private T currentValue = (T) Constants.defaultInitialValueForSpinner;

    public void setCurrentValue(T newValue)
    {
        this.currentValue = newValue;
    }

    public T getCurrentValue()
    {
        return currentValue;
    }
}
