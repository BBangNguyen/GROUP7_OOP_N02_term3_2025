public class UninitializedString {
    String str; // Không khởi tạo giá trị

    void printString() {
        System.out.println("Giá trị của str: " + str);
    }

    public static void main(String[] args) {
        UninitializedString obj = new UninitializedString();
        obj.printString();
    }
}
