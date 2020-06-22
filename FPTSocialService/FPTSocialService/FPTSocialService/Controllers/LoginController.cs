using FPTSocialService.DAO;
using FPTSocialService.DTO;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web;
using System.Web.Http;

namespace FPTSocialService.Controllers
{
    public class LoginController : ApiController
    {

        public bool Post([FromBody] JObject data)
        {

            String username = (string)data["username"];
            String password = (string)data["password"];

            Account account;

            account = AccountDAO.Instance.getAccountByUsernameAndPass(username, password);

            return account == null ? false : true;
        }
    }
}
