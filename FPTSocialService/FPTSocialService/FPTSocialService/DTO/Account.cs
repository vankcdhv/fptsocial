using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace FPTSocialService.DTO
{
    public class Account
    {
        private int id;
        private String username;
        private String password;
        private String mail;
        private bool status;

        public Account()
        {
        }

        public Account(int iD, string username, string password, String mail, bool status)
        {
            this.Id = iD;
            this.Username = username;
            this.Password = password;
            this.mail = mail;
            this.Status = status;
        }

        public int Id { get => id; set => id = value; }
        public string Username { get => username; set => username = value; }
        public string Password { get => password; set => password = value; }
        public bool Status { get => status; set => status = value; }
        public string Mail { get => mail; set => mail = value; }
    }
}