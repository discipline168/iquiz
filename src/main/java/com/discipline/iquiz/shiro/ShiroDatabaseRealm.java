package com.discipline.iquiz.shiro;

import com.auth0.jwt.interfaces.Claim;
import com.discipline.iquiz.mapper.UserMapper;
import com.discipline.iquiz.jwt.JWTToken;
import com.discipline.iquiz.jwt.util.JWTUtil;
import com.mysql.jdbc.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Map;

public class ShiroDatabaseRealm extends AuthorizingRealm {

    @Resource
    UserMapper userMapper;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        int role = JWTUtil.getInfoByToken(principalCollection.toString()).get("role").asInt();

        ArrayList<String> roles = new ArrayList<>();
        //角色授权
        if(role==1)
            roles.add("student");
        else if(role==2)
            roles.add("teacher");

        ArrayList<String> perms = new ArrayList<>();
        //perms.add("class:add");

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addRoles(roles);
        info.addStringPermissions(perms);

        return info;
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String token = (String) authenticationToken.getCredentials();
        if(StringUtils.isNullOrEmpty(token))
            throw new UnknownAccountException();
        else{
            Map<String, Claim> claimMap = JWTUtil.getInfoByToken(token);
            if(claimMap==null)
                throw new UnknownAccountException();
            else{

                //验证token是否失效
                if(JWTUtil.isTokenExpired(claimMap.get("exp").asDate()))
                    throw new ExpiredCredentialsException();
                else{
                    String id = claimMap.get("id").asString();

                    String pwd = userMapper.getPwdById(id);
                    if(StringUtils.isNullOrEmpty(pwd))
                        throw new UnknownAccountException();
                    else
                        if(!JWTUtil.verify(token,id,pwd))
                            throw new DisabledAccountException();


                }
            }
        }
        return new SimpleAuthenticationInfo(token,token,"realm");
    }
}
