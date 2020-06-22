using FPTSocialService.DTO;
using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Web;

namespace FPTSocialService.DAO
{
    public class AccountDAO
    {
        private static AccountDAO instance;
        private AccountDAO() { }

        internal static AccountDAO Instance
        {
            get
            {
                if (instance == null) instance = new AccountDAO();
                return AccountDAO.instance;
            }
            private set
            {
                instance = value;
            }
        }

        public List<Account> GetAllUsers()
        {
            List<Account> userList = new List<Account>();

            String query = "SELECT * FROM Accounts";

            DataTable data = DataProvider.Instance.excuteQuery(query, null);

            foreach (DataRow dr in data.Rows)
            {
                int id = (int)dr["id"];
                String username = dr["username"].ToString();
                String password = dr["password"].ToString();
                String mail = dr["mail"].ToString();
                bool status = bool.Parse(dr["status"].ToString());
                userList.Add(new Account(id, username, password, mail, status));
            }

            return userList;
        }

        public Account getAccountByUsernameAndPass(String username, String password)
        {
            Account account = null;

            String query = "EXEC GetAccountByUsernameAndPass @username , @password";
            DataTable data = DataProvider.Instance.excuteQuery(query, new object[] { username, password });

            foreach (DataRow dr in data.Rows)
            {
                int id = (int)dr["id"];
                String user = dr["username"].ToString();
                String pass = dr["password"].ToString();
                String mail = dr["mail"].ToString();
                bool status = bool.Parse(dr["status"].ToString());
                account = new Account(id, user, pass, mail, status);
            }

            return (account != null ? account : null);
        }
    }
}