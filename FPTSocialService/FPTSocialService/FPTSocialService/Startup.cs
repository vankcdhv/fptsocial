using System;
using System.IdentityModel.Tokens;
using System.Threading.Tasks;
using Microsoft.Owin;
using Microsoft.Owin.Security;
using Microsoft.Owin.Security.Jwt;
using Owin;

[assembly: OwinStartup(typeof(FPTSocialService.Startup))]

namespace FPTSocialService
{
    public class Startup
    {
        public void Configuration(IAppBuilder app)
        {
            app.UseJwtBearerAuthentication(
                new JwtBearerAuthenticationOptions
                {
                    
                });
        }
    }
}
