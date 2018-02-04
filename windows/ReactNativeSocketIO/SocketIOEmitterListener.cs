using Newtonsoft.Json.Linq;
using Quobject.EngineIoClientDotNet.ComponentEmitter;
using ReactNative.Modules.Core;
using ReactNative.Bridge;

namespace ReactNativeSocketIO
{
    internal class SocketIOEmitterListener: IListener
    {
        private static int _count = 0;

        private string _eventName;
        private ReactContext _context;

        private int _id;

        public SocketIOEmitterListener(string e, ref ReactContext c)
        {
            _eventName = e;
            _context = c;

            _id = _count;

            _count++;
        }

        public void Call(params object[] args)
        {
            JObject p = new JObject();

            p.Add("name", _eventName);
            p.Add("items", new JArray(args));

            _context.GetJavaScriptModule<RCTDeviceEventEmitter>().emit("socketEvent", p);
        }

        public int CompareTo(IListener other)
        {
            if (_id < other.GetId())
                return -1;
            else if (_id == other.GetId())
                return 0;
            else
                return 1;
        }

        public int GetId()
        {
            return _id;
        }

    }
}
