package FirstTaxi;

import java.util.ArrayList;

class TreeNode {
    /* Overview: 树节点类，包含父节点、子节点与ID属性
     */

    public ArrayList<TreeNode> children;
    public TreeNode father;
    public int id;

    public boolean repOK() {
        /* @ Effects: \result==invariant(this).
        */
        if (father == null){
            return false;
        }

        if (id < 0){
            return false;
        }

        return true;
    }

    public TreeNode(TreeNode father, int id) {
        /* @ REQUIRES: (father != null) & (id >= 0);
        @ MODIFIES: 所有属性;
        @ EFFECTS: 初始化所有属性;
        */

        children = new ArrayList<>();
        this.father = father;
        this.id = id;
    }
}
