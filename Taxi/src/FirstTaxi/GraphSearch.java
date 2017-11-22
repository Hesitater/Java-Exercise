package FirstTaxi;

import java.util.LinkedList;
import static FirstTaxi.TaxiTest.MAXORDER;


public class GraphSearch {
    /* Overview: GraphSearch可根据输入的起点与终点计算并存储最短路径
     */

    private MapNode[] newMap;
    private LinkedList<TreeNode> pathList;
    //private LinkedList<MyInt> distancePathList;
    private boolean[] visit;

    public boolean repOK() {
        /* @ Effects: \result==invariant(this).
        */

        if (newMap == null) {
            return false;
        }

        return true;
    }

    public GraphSearch(MapNode[] newMap) {
        /* @ REQUIRES: newMap != null;
        @ MODIFIES: 所有属性;
        @ EFFECTS: 初始化所有属性；
        */

        this.newMap = newMap;
    }

    public synchronized LinkedList<MyInt> findPath(int startPointIndex, int destinationIndex) {
        /* @ REQUIRES: (0 <= startPointIndex < 6400) & (0 <= destinationIndex < 6400);
        @ MODIFIES: this.pathList, this.visit;
        @ EFFECTS: 寻找从startPointIndex到destinationIndex最短路径并返回；
        @ THREAD_REQUIRES: None;
        @ THREAD_EFFECTS: \locked(this.pathList);
        */

        visit = new boolean[MAXORDER * MAXORDER];
        pathList = new LinkedList<TreeNode>();
        TreeNode tree = new TreeNode(null, startPointIndex);
        pathList.add(tree);
        visit[startPointIndex] = true;

        if (startPointIndex != destinationIndex) {
            BFS(destinationIndex); //寻找最短路径
        }

        //从尾部到头部遍历最短路径链表，得到最短路径的点链表path
        if (pathList.getLast().id == destinationIndex) {
            LinkedList<MyInt> path = new LinkedList<>();
            TreeNode treeNode = pathList.getLast();
            MyInt index = new MyInt(treeNode.id);
            path.addFirst(index);
            while (path.getFirst().getValue() != startPointIndex) {
                treeNode = treeNode.father;
                index = new MyInt(treeNode.id);
                path.addFirst(index);
            }
            return path;
        } else {
            return null;
        }
    }

    private synchronized void BFS(int destinationIndex) {
        /* @ REQUIRES: 0 <= destinationIndex < 6400;
        @ MODIFIES: this.pathList, this.visit;
        @ EFFECTS: 寻找从当前pathList内所有点到destinationIndex的最短路径；
        @ THREAD_REQUIRES: None;
        @ THREAD_EFFECTS: \locked(this.pathList);
        */

        while (pathList.size() != 0) {
            int lastSize = pathList.size();

            for (int i = 0; i < lastSize; i++) {
                TreeNode fatherT = pathList.get(i);
                MapNode fatherM = newMap[fatherT.id];
                //遍历该节点所有邻居节点
                for (int j = 0; j < fatherM.getDegree(); j++) {
                    int neighbor = fatherM.getConnectedNodeIndex(j);
                    if (visit[neighbor]) {
                        //此节点已被遍历，忽略
                    } else if (neighbor == destinationIndex) { //找到目标节点
                        visit[neighbor] = true;
                        TreeNode tree = new TreeNode(fatherT, neighbor);
                        pathList.add(tree);
                        fatherT.children.add(tree);
                        return;
                    } else {
                        visit[neighbor] = true;
                        TreeNode tree = new TreeNode(fatherT, neighbor);
                        pathList.add(tree);
                        fatherT.children.add(tree);
                    }
                }
            }

            //删除已经搜索过的节点
            for (int i = 0; i < lastSize; i++) {
                pathList.removeFirst();
            }
        }
    }

}
