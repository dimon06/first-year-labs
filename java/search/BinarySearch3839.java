package search;

public class BinarySearch3839 {
    // Pre: args != null && args.length >= 1 && forall i = 0...args.length - 1: isInt(args[i]) == true
    // exists ind : forall i = 0...ind - 1: int(args[i]) < int(args[i + 1])
    // forall i = ind + 1...args.length - 2: int(args[i]) > int(args[i + 1])
    public static void main(String[] args) {
        // args.length > 0
        int[] a = new int[args.length];
        // args.length > 0 && args.length > i && i == i' + 1
        for (int i = 0; i < args.length; i++) {
            // i >= 0 && i < args.length
            a[i] = Integer.parseInt(args[i]);
            // a[i] == int(args[i]) && i == i' + 1
        }
        // i == args.length && for all i = 0...a.length-1: a[i] == int(args[i])
        // forall x abs(x) < INF && a[-1] = -INF && a[a.length] = -INF ->  a[-1] < a[0] && a[a.length - 1] > a[a.length]
        int ans;
        if (a.length % 2 == 0) {
            // a.length % 2 == 0
            ans = recursionBinarySearch(a);
            //
        } else {
            // a.length % 2 == 1
            ans = iterativeBinarySearch(a);
        }
        // ans >= 0
        // forall i = 0...a.length: a[ans] >= a[i] && not exists ans2: ans2 < ans && a[ans2] >= a[ans]
        System.out.print(ans);
    }
    // Post: ans >= 0 && forall i = 0...a.length: a[ans] >= a[i] && not exists ans2: ans2 < ans && a[ans2] >= a[ans]

    // Pre: a != null && a.length > 0
    // exists ind : forall i = 0...ind-1: int(args[i]) < int(args[i+1])
    // forall i = ind+1...args.length-2: int(args[i]) > int(args[i+1])
    public static int iterativeBinarySearch(int[] a) {
        // a.length > 0
        int l = -1, r = a.length - 1;
        // a[l] < a[l + 1] && a[r] >= a[r + 1] && r - l > 1
        while (r - l > 1) {
            // r - l > 1
            // r - l >= 2
            // (r - l) / 2 >= 1
            int mid = (r + l) / 2;
            // mid = l + (r - l) / 2  -> mid >= l + 1  -> mid > l
            // mid = r - (r - l) / 2  -> mid <= r - 1  -> mid < r
            if (a[mid] < a[mid + 1]) {
                // a[mid + 1] > a[mid] && a[r] <= x
                // forall i = -1...mid: a[i] < a[i + 1]
                l = mid;
                // l = mid && r = r'
            } else {
                // a[l] < a[l + 1] && a[mid] >= a[mid + 1]
                // forall i = mid...a.length-1: a[i] >= a[i + 1]
                r = mid;
                // l = l' && r = mid
            }
            //NOTE: no information about asymptotic
            // [l, r] in [l', r'] && [l, r] != [l', r'] -> loop is not endless
        }
        // r - l == 1 && a[l] < a[l + 1] && a[r] >= a[r + 1]
        // forall i = -1...l: a[i] < a[i + 1]
        // forall i = r...a.length-1: a[i] >= a[i + 1]
        // a[l] < a[r] && a[r] >= a[r + 1] && r - the first maximum in array
        // r > l && l >= -1 ->  r >= 0
        return r;
    }
    // Post: r >= 0 && forall i = 0...a.length: a[ans] >= a[i] && not exists ans2: ans2 < ans && a[ans2] >= a[ans]

    // Pre: a != null && a.length > 0
    // exists ind : forall i = 0...ind-1: int(args[i]) < int(args[i+1])
    // forall i = ind+1...args.length-2: int(args[i]) > int(args[i+1])
    public static int recursionBinarySearch(int[] a) {
        return recursionBinarySearch(a, -1, a.length - 1);
    }
    // Post: ans >= 0 && forall i = 0...a.length: a[ans] >= a[i] && not exists ans2: ans2 < ans && a[ans2] >= a[ans]

    // Pre: a != null && a.length > 0
    // exists ind : forall i = 0...ind-1: int(args[i]) < int(args[i+1])
    // forall i = ind+1...args.length-2: int(args[i]) > int(args[i+1])
    public static int recursionBinarySearch(int[] a, int l, int r) {
        // a[l] < a[l + 1] && a[r] >= a[r + 1] && r - l > 1
        if (l + 1 == r) {
            // r - l == 1 && a[l] < a[l + 1] && a[r] >= a[r + 1]
            // forall i = -1...l: a[i] < a[i + 1]
            // forall i = r...a.length-1: a[i] >= a[i + 1]
            // a[l] < a[r] && a[r] >= a[r + 1] && r - the first maximum in array
            return r;
            // r > l && l >= -1 ->  r >= 0
        }
        // r - l > 1
        // r - l >= 2
        // (r - l) / 2 >= 1
        int mid = (r + l) / 2;
        // mid = l + (r - l) / 2  -> mid >= l + 1  -> mid > l
        // mid = r - (r - l) / 2  -> mid <= r - 1  -> mid < r
        if (a[mid] < a[mid + 1]) {
            // a[mid + 1] > a[mid] && a[r] <= x
            // forall i = -1...mid: a[i] < a[i + 1]
            return recursionBinarySearch(a, mid, r);
            // l = mid && r = r'
        }
        // a[l] < a[l + 1] && a[mid] >= a[mid + 1]
        // forall i = mid...a.length-1: a[i] >= a[i + 1]
        return recursionBinarySearch(a, l, mid);
        // l = l' && r = mid
        // [l, r] in [l', r'] && [l, r] != [l', r'] -> recursion is not endless
    }
    // Post: ans >= 0 && forall i = 0...a.length: a[ans] >= a[i] && not exists ans2: ans2 < ans && a[ans2] >= a[ans]
}
