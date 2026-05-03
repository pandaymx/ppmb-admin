public class test {
    public static void main(String[] args) {
        System.out.println("123456789012345678".replaceAll("(\\d{4})\\d{10}(\\w{4})", "$1****$2"));
    }
}
