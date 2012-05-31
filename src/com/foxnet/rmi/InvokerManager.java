package com.foxnet.rmi;

import java.io.IOException;

import com.foxnet.rmi.binding.RemoteBinding;
import com.foxnet.rmi.binding.registry.DynamicRegistry;
import com.foxnet.rmi.binding.registry.StaticRegistry;

public abstract class InvokerManager {

	public abstract Object remoteToLocal(Object argument);

	public abstract Object localToRemote(Object argument);

	public Object[] localsToRemotes(Object... localArguments) {
		if (localArguments != null) {
			for (int i = 0; i < localArguments.length; i++) {
				localArguments[i] = localToRemote(localArguments[i]);
			}
		}
		return localArguments;
	}

	public Object[] remotesToLocals(Object... remoteArguments) {
		if (remoteArguments != null) {
			for (int i = 0; i < remoteArguments.length; i++) {
				remoteArguments[i] = remoteToLocal(remoteArguments[i]);
			}
		}
		return remoteArguments;
	}

	public abstract StaticRegistry statically();

	public abstract DynamicRegistry dynamically();

	public abstract Invoker invoker(RemoteBinding remoteBinding);

	public abstract Invoker lookupInvoker(String target) throws IOException;

	public Object lookupProxy(String target) throws IOException {
		return lookupInvoker(target).proxy();
	}
}
