using FPTSocialService.DAO;
using FPTSocialService.DTO;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;

namespace FPTSocialService.Controllers
{
    public class AccountsController : ApiController
    {
        public List<Account> Get()
        {
            return AccountDAO.Instance.GetAllUsers();
        }
    }
}
