import java.util.*;


/*
 I provided an expiration check in get function.
 It won't keep checking continuously and
 delete the value instantly after it expires.
 If we wanted to check expiration continuously,
 we would have to create a new thread.
 This would make the codes way too complicated (break simplicity),
 and the codes would become brittle.
 */

public class LRUCache<K, V> {
    private Map<K, Node> cacheMap = new HashMap<>();  // Used to store values
    private Deque<K> keys = new LinkedList<>();       // Used to store the order, the head is the least recently used cache
    private int capacity;

    public LRUCache(int pCapacity){
        capacity = pCapacity;
    }


    public V get(K key){
        if(keys.contains(key) && cacheMap.containsKey(key)){
            Node<K, V> temp = cacheMap.get(key);
            keys.remove(key);
            // Check if cache expired
            if(temp.expire()){
                cacheMap.remove(key);
                System.out.println("Cache expires.");
                return null;
            }else {
                // Update the cache to be the latest used one
                keys.addLast(key);
                return temp.getValue();
            }
        }
        return null;
    }


    public void write(K key, V value, Date date){
        // if the key already exists, update value and expire date
        if(keys.contains(key) && cacheMap.containsKey(key)){
            Node<K, V> temp = cacheMap.get(key);
            temp.setValue(value);
            temp.setExpireTime(date);

            if(!key.equals(keys.getLast())){
                keys.remove(key);
                keys.addLast(key);
            }
        }else{
            Node<K, V> newNode = new Node<>(key, value, date);
            cacheMap.put(key, newNode);
            keys.addLast(key);

            if(capacity < this.size()){
                K firstKey = keys.getFirst();
                cacheMap.remove(firstKey);
                keys.removeFirst();
            }
        }
    }

    public void write(Node<K, V> pNode){
        if(keys.contains(pNode.getKey()) && cacheMap.containsKey(pNode.getKey())){
            keys.remove(pNode.getKey());
            keys.addLast(pNode.getKey());
            // Update value in cacheMap
            cacheMap.put(pNode.getKey(), pNode);
        }else{
            cacheMap.put(pNode.getKey(), pNode);
            keys.addLast(pNode.getKey());

            if(capacity < this.size()){
                K firstKey = keys.getFirst();
                cacheMap.remove(firstKey);
                keys.removeFirst();
            }
        }
    }


    public V remove(K key){
        if(keys.contains(key) && cacheMap.containsKey(key)){
            Node<K, V> temp = cacheMap.remove(key);
            cacheMap.remove(key);
            keys.remove(key);
            return temp.getValue();
        }
        return null;
    }

    public int size(){
        return cacheMap.size();
    }

    public String toString(){
        StringBuilder str = new StringBuilder();

        for (K key: keys){
            String temp = cacheMap.get(key).toString();
            str.append(temp);
            str.append("\n");
        }

        return str.toString();
    }

    public void clear(){
        cacheMap.clear();
        keys.clear();
    }


}

class Node<K, V>{
    private K key;
    private V value;
    private Date expireTime;

    public Node (K pKey, V pValue, Date pExpire){
        this.key = pKey;
        this.value = pValue;
        this.expireTime = pExpire;
    }

    public void setValue(V pValue){
        this.value = pValue;
    }

    public void setExpireTime(Date pDate){
        this.expireTime = pDate;
    }

    public V getValue(){
        return this.value;
    }

    public K getKey() { return this.key; }


    public boolean expire(){
        if(this.expireTime.before(new Date())){
            return true;
        }else {
            return false;
        }
    }

    public String toString(){
        return key + ": " + value;
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null){ return false; }
        else if (obj == this){ return true; }
        else if (obj.getClass() != this.getClass()) { return false; }
        else { return ((Node)obj).key.equals(this.key) && ((Node)obj).value.equals(this.value); }
    }

    @Override
    public int hashCode(){
        return this.key.hashCode();
    }
}


