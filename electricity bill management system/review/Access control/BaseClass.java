
package package1;

public class BaseClass {
    protected void protectedMethod() {
        System.out.println("Phương thức protected từ BaseClass");
    }
}

class SamePackageClass {
    public void accessProtected() {
        BaseClass base = new BaseClass();
        base.protectedMethod(); // ✅ Truy cập được do cùng package
    }
}

// ==========================
// Lớp con kế thừa BaseClass (dùng cho bài 4)
// ==========================
package package2;
import package1.BaseClass;

class SubClass extends BaseClass {
    public void accessProtected() {
        protectedMethod(); // ✅ Truy cập được do kế thừa
    }
}
