using System;
using System.Collections.Generic;
using System.Configuration;
using System.Data;
using System.Data.SqlClient;
using System.Linq;
using System.Web;

namespace FPTSocialService.DAO
{
    public class DataProvider
    {
        private static DataProvider instance;

        private String connectionSTR = "";

        public static DataProvider Instance
        {
            get
            {
                if (instance == null) instance = new DataProvider();
                return DataProvider.instance;
            }
            private set
            {
                instance = value;
            }
        }

        private DataProvider()
        {
            connectionSTR = ConfigurationManager.ConnectionStrings["FPTSocialDB"].ToString();
        }

        public DataTable excuteQuery(String query, object[] param)
        {
            DataTable data = new DataTable();
            using (SqlConnection connection = new SqlConnection(connectionSTR))
            {
                connection.Open();
                SqlCommand command = new SqlCommand(query, connection);
                if (param != null)
                {
                    int i = 0;
                    String[] listPara = query.Split(' ');
                    foreach (String item in listPara)
                    {
                        if (item.Contains("@"))
                        {
                            command.Parameters.AddWithValue(item, param[i]);
                            i++;
                        }
                    }
                }
                SqlDataAdapter adapter = new SqlDataAdapter(command);
                adapter.Fill(data);
                connection.Close();
            }
            return data;
        }

        public int excuteNonQuery(String query, object[] param)
        {
            int data = 0;
            using (SqlConnection connection = new SqlConnection(connectionSTR))
            {
                connection.Open();
                try
                {
                    SqlCommand command = new SqlCommand(query, connection);
                    SqlDataAdapter adapter = new SqlDataAdapter(command);

                    if (param != null)
                    {
                        int i = 0;
                        String[] listPara = query.Split(' ');
                        foreach (String item in listPara)
                        {
                            if (item.Contains("@"))
                            {
                                command.Parameters.AddWithValue(item, param[i]);
                                i++;
                            }
                        }
                    }
                    data = command.ExecuteNonQuery();
                }
                catch (Exception ex)
                {
                    return 0;
                }
                connection.Close();
            }
            return data;
        }

        public object excuteScalar(String query, object[] param)
        {
            object data = 0;
            using (SqlConnection connection = new SqlConnection(connectionSTR))
            {
                connection.Open();
                SqlCommand command = new SqlCommand(query, connection);
                SqlDataAdapter adapter = new SqlDataAdapter(command);

                if (param != null)
                {
                    int i = 0;
                    String[] listPara = query.Split(' ');
                    foreach (String item in listPara)
                    {
                        if (item.Contains("@"))
                        {
                            command.Parameters.AddWithValue(item, param[i]);
                            i++;
                        }
                    }
                }
                data = command.ExecuteScalar();
                connection.Close();
            }
            return data;
        }
    }
}