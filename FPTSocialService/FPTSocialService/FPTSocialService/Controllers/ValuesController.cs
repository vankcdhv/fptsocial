using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;

namespace FPTSocialService.Controllers
{
    public class ValuesController : ApiController
    {
        List<String> list = new List<string> { "value1", "value2", "value3", "value4", "value5" };

        // GET api/values
        public IEnumerable<string> Get()
        {
            return list;
        }

        // GET api/values/5
        [HttpGet]
        public string GetByID([FromUri]int id)
        {
            return list[id];
        }

        [HttpPost]
        public string GetByIDPost([FromBody] int id)
        {
            return list[id];
        }

        // POST api/values
        

        // PUT api/values/5
        public void Put(int id, [FromBody] string value)
        {
            list[id] = value;
        }

        // DELETE api/values/5
        public void Delete(int id)
        {
            list.RemoveAt(id);
        }
    }
}
