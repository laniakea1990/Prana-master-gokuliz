/*
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.prana.http.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.prana.service.HostService;
import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;
import io.reactivex.netty.protocol.http.server.RequestHandler;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by dchoudhury on 10/28/14.
 */
public class HostsHandlerTest extends AbstractIntegrationTest {

    private HostService hostService = mock(HostService.class);

    @Override
    protected RequestHandler<ByteBuf, ByteBuf> getHandler() {
        ArrayList<InstanceInfo> instanceInfos = new ArrayList<>();
        instanceInfos.add(InstanceInfo.Builder.newBuilder().setAppName("foo").setVIPAddress("bar").setHostName("host1").build());
        instanceInfos.add(InstanceInfo.Builder.newBuilder().setAppName("foo").setVIPAddress("bar").setHostName("host2").build());
        when(hostService.getHosts("foo")).thenReturn(instanceInfos);
        return new HostsHandler(hostService, new ObjectMapper());
    }

    @Test
    public void shouldReturnAListOfHostsWhenBothVipAndAppIsSpecified() {
        HttpClientRequest<ByteBuf> request = HttpClientRequest.<ByteBuf>createGet("/hosts?appName=foo&vip=bar");
        String response = TestUtils.getResponse(request, client);
        Assert.assertEquals("[\"host1\",\"host2\"]", response);
    }

}
