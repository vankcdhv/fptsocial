using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace FPTSocialService.Provider
{
    public class TokenProviderOptine
    {
        public string Path { get; set; } = "/token"; //line 1

        public TimeSpan Expiration { get; set; } = TimeSpan.FromDays(+1); //line 2

/*        public SigningCredentials SigningCredentials { get; set; }//line 3
*/    }
}