package functions;

import java.lang.reflect.Array;
import java.util.*;

public class UsefulFunction
{
    public static void throwException(String message)
    {
        try {
            throw new Exception(message);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void fillupMap(Map<Integer, List<Integer>> map, int key, int newValue)
    {
        if (map.isEmpty()) {
            map.put(key, Arrays.asList(newValue));
            return;
        }

        for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
            if (entry.getKey() == key) {
                List<Integer> tempList = new ArrayList<>();

                for (Integer v : entry.getValue()) {
                    tempList.add(v);
                }

                tempList.add(newValue);

                map.put(key, tempList);
            }
        }
    }

    public static void fillUpMapForManyParents(Map<Integer, List<Integer>> map, int fromIndex, int newValue, int amountAllBackPaths)
    {
        if (map.isEmpty()) {
            for (int key = fromIndex; key < amountAllBackPaths; key++) {
                map.put(key, Arrays.asList(newValue));
            }
        }
        else {
            for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
                if (entry.getKey() >= fromIndex) {
                    List<Integer> tempList = new ArrayList<>();

                    for (Integer v : entry.getValue()) {
                        tempList.add(v);
                    }

                    tempList.add(newValue);

                    map.put(entry.getKey(), tempList);
                }
            }
        }
    }

    public static void printMap(Map<Integer, List<Integer>> map)
    {
        for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
            System.out.println("key = " + entry.getKey());
            for (Integer parent : entry.getValue()) {
                System.out.print(parent + "->");
            }
            System.out.println();
        }
    }
}
