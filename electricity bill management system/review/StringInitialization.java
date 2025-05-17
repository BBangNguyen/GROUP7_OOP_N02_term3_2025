public class StringInitialization {
    String str1 = "Khởi tạo trực tiếp";  // Khởi tạo tại điểm định nghĩa
    String str2;  // Khởi tạo trong constructor

    // Constructor
    StringInitialization() {
        str2 = "Khởi tạo trong constructor";
    }

    void printStrings() {
        System.out.println("str1: " + str1);
        System.out.println("str2: " + str2);
    }

    public static void main(String[] args) {
        StringInitialization obj = new StringInitialization();
        obj.printStrings();
    }
}
