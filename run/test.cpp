#include <bits/stdc++.h>
using namespace std;
long double a, b, c, d;
long long x;

long double A, B, C;
#define f(x) min(1000000000000000001.0L, floor(A * x * x + B * x + C))
bool check(long long n) {
    A = (b + d) * 0.5L;
    B = (a - c - d * n + (d - b) * 0.5L);
    C = (c * n + d * 0.5L * n * n - d * 0.5L * n);
    long long X1 = floor(-B / A * 0.5L), X2 = ceil(-B / A * 0.5L);
    X1 = max(0LL, min(X1, n));
    X2 = max(0LL, min(X2, n));
    long long Y = min(min(f(0), f(n)), min(f(X1), f(X2)));
    return x >= Y;
}
int q;
namespace fastread{
	const int LEN=1000000;
	char in[LEN+5];
	char *pin=in,*ed=in;
	inline char gc(void){
		return pin==ed&&(ed=(pin=in)+fread(in,1,LEN,stdin),ed==in)?EOF:*pin++;
	}
	template<typename T> inline void read(T &x){
		static int f;
		static char c;
		c=gc(),f=1,x=0;
		while(c<'0'||c>'9') f=(c=='-'?-1:1),c=gc();
		while(c>='0'&&c<='9') x=10*x+c-'0',c=gc();
		x*=f;
	}
}
using fastread::read;
int main() {
    freopen("money.in", "r", stdin);
    freopen("money.out", "w", stdout);
    read(q);
    while (q--) {
        read(a); read(b); read(c); read(d); read(x);
        long long l = 0, r = 1000000000000000000;
        while (l < r) {
            long long mid = (l + r + 1) >> 1;
            if (check(mid)) l = mid;
            else r = mid - 1;
        }

        printf("%lld\n", l);
    }
    return 0;
}