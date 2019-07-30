using Quobject.SocketIoClientDotNet.Client;
using ReactNative.Bridge;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ReactNativeSocketIO
{
    internal class SocketIOAckCallback: IAck
    {
        private ICallback _callback;

        public SocketIOAckCallback(ICallback callback)
        {
            _callback = callback;
        }

        public void Call(params object[] args)
        {
            _callback.Invoke(args);
        }
    }
}
