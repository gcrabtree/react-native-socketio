using Newtonsoft.Json.Linq;
using Quobject.SocketIoClientDotNet.Client;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ReactNativeSocketIO
{
    public static class SocketIOUtils
    {
        public static IO.Options JObjectToOptions(JObject options)
        {
            IO.Options opts = new IO.Options();

            foreach (var item in options)
            {
                string key = item.Key.ToLower();
                switch (key)
                {
                    case "force new connection":
                    case "forcenew":
                        opts.ForceNew = options.Value<bool>(key);
                        break;
                    case "multiplex":
                        opts.Multiplex = options.Value<bool>(key);
                        break;
                    case "reconnection":
                        opts.Reconnection = options.Value<bool>(key);
                        break;
                    case "connect_timeout":
                        opts.Timeout = options.Value<long>(key);
                        break;
                    case "reconnectionattempts":
                        opts.ReconnectionAttempts = options.Value<int>(key);
                        break;
                    case "reconnectiondelay":
                        opts.ReconnectionDelay = options.Value<int>(key);
                        break;
                    case "reconnectiondelaymax":
                        opts.ReconnectionDelayMax = options.Value<int>(key);
                        break;
                }
            }

            return opts;
        }

        public static Dictionary<string, object> ToDictionnary(JObject o)
        {
            Dictionary<string, object> dic = new Dictionary<string, object>();

            foreach (var item in o)
            {
                dic.Add(item.Key, item.Value);
            }

            return dic;
        }
    }
}
