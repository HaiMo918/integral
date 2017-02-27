package com.integral.utils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.cookie.CookieStore;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by xiongzicheng on 2016/11/1.
 */
public class XCookieStore implements CookieStore {

    public static ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static LoadingCache<String, List<Cookie>> cookieCache = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterAccess(15, TimeUnit.MINUTES)//多久时间没被访问就清除掉
            .build(new CacheLoader<String, List<Cookie>>() {
                @Override
                public List<Cookie> load(String key) throws Exception {
                    return new ArrayList<>();
                }
            });

    public static List<Cookie> getCookieSet() {
        String uid=threadLocal.get();
        if (uid==null){
            uid="default";
        }
       return cookieCache.getUnchecked(uid);
    }

    /**
     * Adds an {@link Cookie}, replacing any existing equivalent cookies.
     * If the given cookie has already expired it will not be added, but existing
     * values will still be removed.
     *
     * @param cookie the {@link Cookie cookie} to be added
     */
    @Override
    public synchronized void addCookie(Cookie cookie) {
        List<Cookie> cookies = getCookieSet();

        for (final Iterator<Cookie> it = cookies.iterator(); it.hasNext(); ) {
            Cookie cke = it.next();
            if (cke.getDomain().equals(cookie.getDomain())) {
                if (cke.getPath().equals(cookie.getPath())) {
                    if (cke.getName().equals(cookie.getName())) {
                        it.remove();
                    }
                }
            }
        }

        cookies.add(cookie);
    }

    /**
     * Returns all cookies contained in this store.
     *
     * @return all cookies
     */
    @Override
    public synchronized List<Cookie> getCookies() {
        List<Cookie> cookies = getCookieSet();
        return new ArrayList<>(cookies);
    }

    /**
     * Removes all of {@link Cookie}s in this store that have expired by
     * the specified {@link Date}.
     *
     * @param date
     * @return true if any cookies were purged.
     */
    @Override
    public synchronized boolean clearExpired(Date date) {
        if (date == null) {
            return false;
        }
        boolean removed = false;
        List<Cookie> cookies = getCookieSet();
        for (final Iterator<Cookie> it = cookies.iterator(); it.hasNext(); ) {
            if (it.next().isExpired(date)) {
                it.remove();
                removed = true;
            }
        }
        return removed;
    }

    /**
     * Clears all cookies.
     */
    @Override
    public synchronized void clear() {
        List<Cookie> cookies = getCookieSet();
        cookies.clear();
    }
}
