//© 2021 Sean Murdock

package com.getsimplex.steptimer.service;


/**
 * Created by Administrator on 12/7/2016.
 */
public class TokenController {

    public static boolean validateToken(String token) throws Exception {
        return !TokenService.isExpired(token);
    }
}
