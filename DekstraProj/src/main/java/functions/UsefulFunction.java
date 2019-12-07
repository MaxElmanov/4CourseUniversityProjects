package functions;

import objects.DekstraNode;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

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

    public static void fillUpMapForManyParents(Map<Integer, List<Integer>> map, int fromIndex, int newValue, int toIndex)
    {
        if (map.isEmpty()) {
            for (int key = fromIndex; key < toIndex; key++) {
                map.put(key, Arrays.asList(newValue));
            }
        }
        else {
            for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
                int key = entry.getKey();
                if (key >= fromIndex && key < toIndex) {
                    List<Integer> tempList = new ArrayList<>();

                    for (Integer v : entry.getValue()) {
                        tempList.add(v);
                    }

                    tempList.add(newValue);

                    map.put(key, tempList);
                }
            }
        }
    }

    public static void fillUpMapByList(Map<Integer, List<Integer>> map, int pathNumber, List<Integer> paths)
    {
        List<Integer> tempList = new ArrayList<>();

        boolean wasInside = false;
        for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
            if (entry.getKey() == pathNumber) {
                wasInside = true;

                for (Integer number : entry.getValue()) {
                    if (!tempList.contains(number)) {
                        tempList.add(number);
                    }
                }
            }
            if (wasInside) {
                break;
            }
        }

        for (Integer number : paths) {
            if (!tempList.contains(number)) {
                tempList.add(number);
            }
        }

        map.put(pathNumber, tempList);
    }

    public static void printMap(Map<Integer, List<Integer>> map)
    {
        for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
            System.out.println("key = " + entry.getKey());
            for (Integer nodeNumber : entry.getValue()) {
                System.out.print(nodeNumber + "->");
            }
            System.out.println();
        }
    }

    public static boolean isLastIndex(int index, List<Integer> parentNodesNumbers)
    {
        return (parentNodesNumbers.size() - 1) == index
               ? true
               : false;
    }

    public static void removeExistingItemFromListByIndex(List<Integer> paths, Integer element)
    {
        if (paths.contains(element)) {
            paths.remove(element);
        }
    }

    public static void removeExistingItemFromListByIndex(List<Integer> paths, Integer element, List<Integer> besidesList)
    {
        if(besidesList.contains(element)) {
            return;
        }

        //invokes this method if element doesn't equal every item of "besidesList" which consists of startPoint and EndPoint
        removeExistingItemFromListByIndex(paths, element);
    }

    public static int getExistingElementIndexIn(List<Integer> listOfMap, DekstraNode node)
    {
        for (int i = 0; i < listOfMap.size(); i++){
            if(listOfMap.get(i) == node.getNumber()) {
                return i;
            }
        }

        return -1;
    }

    public static boolean elementExistsIn(List<Integer> list, int element)
    {
        if(list.contains(element)) {
            return true;
        }

        return false;
    }

    public static Integer generateNewPathNumberOf(ConcurrentMap<Integer, List<Integer>> map, Vector<Integer> listOfUsedPathNumbers)
    {
        for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
            Integer key = entry.getKey();
            if(!listOfUsedPathNumbers.contains(key)) {
                return key;
            }
        }

        return null;
    }
}
