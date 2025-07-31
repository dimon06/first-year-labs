package search;

public class BinarySearch {
    // Pre: args != null && args.length >= 2 && forall i = 1...args.length-1: isInt(args[i]) == true
    public static void main(String[] args) {
        // args.length > 0
        int x = Integer.parseInt(args[0]);
        // x == args[0]
        // args.length - 1 > 0
        int[] a = new int[args.length-1];
        // a.length = args.length - 1 && a.length > 0
        // args.length > 1 && args.length > i && i == i' + 1
        for (int i = 1; i < args.length; i++) {
            // i - 1 >= 0 && i < args.length
            a[i-1] = Integer.parseInt(args[i]);
            // a[i-1] == args[i] && i == i' + 1
        }
        // i == args.length && for all i = 0...a.length-1: a[i] == args[i + 1]
        // forall x abs(x) < INF && a[-1] = INF && a[n] = -INF
        // forall i = -1...a.length-1: a[i] >= a[i + 1]
        int ans = recursionBinarySearch(x, a);
        // a[ans] <= x && ans >= 0 && a[ans - 1] > x
        System.out.print(ans);
    }
    // Post: a[ans] <= x && ans >= 0 && a[ans - 1] > x

    // Pre: a != null && a.length > 0 && forall i = -1...a.length-1: a[i] >= a[i + 1]
    public static int iterativeBinarySearch(int x, int[] a) {
        // a.length > 0
        int l = -1, r = a.length;
        // forall i = -1...a.length-1: a[i] >= a[i + 1]
        // a[l] > x && a[r] <= x && r > 0 && r - l > 1
        while (r - l > 1) {
            // r - l > 1
            // r - l >= 2
            // (r - l) / 2 >= 1
            int mid = (r + l) / 2;
            // mid = l + (r - l) / 2  -> mid >= l + 1  -> mid > l
            // mid = r - (r - l) / 2  -> mid <= r - 1  -> mid < r
            // mid != l && mid != r && forall i = -1...a.length-1: a[i] >= a[i + 1]
            if (a[mid] > x) {
                // a[mid] > x && a[r] <= x
                // forall i = -1...mid: a[i] > x
                l = mid;
                // l = mid && r = r'
            } else {
                // a[l] > x && a[mid] <= x
                // forall i = mid...a.length: a[i] <= x
                r = mid;
                // l = l' && r = mid
            }
            // [l, r] in [l', r'] && [l, r] != [l', r'] -> loop is not endless
        }
        // r - l == 1 && a[l] > x && a[r] <= x
        // r > l && l >= -1 ->  r >= 0
        return r;
    }
    // Post: a[r] <= x && r >= 0 && a[r - 1] > x

    // Pre: a != null && a.length > 0 && forall i = -1...a.length-1: a[i] >= a[i + 1]
    public static int recursionBinarySearch(int x, int[] a) {
        // a[-1] > x && a[a.length] <= x && a.length > 0
        return recursionBinarySearch(x, a, -1, a.length);
        // res > l && l >= -1 ->  res >= 0
    }
    // Post: a[res] <= x && res >= 0 && a[res - 1] > x

    // Pre: a != null && a.length > 0 && forall i = -1...a.length-1: a[i] >= a[i + 1] && a[l] > x && a[r] <= x
    public static int recursionBinarySearch(int x, int[] a, int l, int r) {
        if (l + 1 == r) {
            // r - l == 1
            return r;
            // a[l] > x && a[r] <= x && r - l == 1
            // a[r - 1] > x && a[r] <= x
            // r > l && l >= -1 ->  r >= 0
        }
        // r - l > 1
        // r - l >= 2
        // (r - l) / 2 >= 1
        int mid = (r + l)/2;
        // mid = l + (r - l) / 2  -> mid >= l + 1  -> mid > l
        // mid = r - (r - l) / 2  -> mid <= r - 1  -> mid < r
        // mid != l && mid != r
        if (a[mid] > x) {
            // a[mid] > x && a[r] <= x
            // forall i = -1...mid: a[i] > x
            return recursionBinarySearch(x, a, mid, r);
            // l = mid && r = r'
        }
        // a[l] > x && a[mid] <= x
        // forall i = mid...a.length: a[i] <= x
        return recursionBinarySearch(x, a, l, mid);
        // l = l' && r = mid
        // [l, r] in [l', r'] && [l, r] != [l', r'] -> recursion is not endless
    }
    // Post: a[res] <= x && res >= 0 && a[res - 1] > x
}