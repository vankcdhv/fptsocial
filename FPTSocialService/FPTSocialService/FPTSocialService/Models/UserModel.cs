using System;
using System.Collections.Generic;
using System.Data.SqlTypes;
using System.Linq;
using System.Web;

namespace FPTSocialService.Models
{
    public class UserModel
    {
        private String username;
        private String firstname;
        private String lastname;
        private String mail;
        private Boolean gender;
        private SqlDateTime dob;
        private int course;
        private String department;
        private String avatar;
        private String cover;
        private String startdate;

        public UserModel(string username, string firstname, string lastname, string mail, bool gender, SqlDateTime dob, int course, string department, string avatar, string cover, string startdate)
        {
            this.Username = username;
            this.Firstname = firstname;
            this.Lastname = lastname;
            this.Mail = mail;
            this.Gender = gender;
            this.Dob = dob;
            this.Course = course;
            this.Department = department;
            this.Avatar = avatar;
            this.Cover = cover;
            this.Startdate = startdate;
        }

        public string Username { get => username; set => username = value; }
        public string Firstname { get => firstname; set => firstname = value; }
        public string Lastname { get => lastname; set => lastname = value; }
        public string Mail { get => mail; set => mail = value; }
        public bool Gender { get => gender; set => gender = value; }
        public SqlDateTime Dob { get => dob; set => dob = value; }
        public int Course { get => course; set => course = value; }
        public string Department { get => department; set => department = value; }
        public string Avatar { get => avatar; set => avatar = value; }
        public string Cover { get => cover; set => cover = value; }
        public string Startdate { get => startdate; set => startdate = value; }
    }
}