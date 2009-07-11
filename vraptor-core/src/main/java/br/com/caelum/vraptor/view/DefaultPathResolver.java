/***
 *
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.view;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * The default vraptor3 path resolver uses the type and method name as
 * "/TypeName/methodName.result.jsp".
 *
 * @author Guilherme Silveira
 * @author Sérgio Lopes
 * @author Jonas Abreu
 */
@RequestScoped
public class DefaultPathResolver implements PathResolver {

	private final HttpServletRequest request;
	private final AcceptHeaderToFormat acceptHeaderToFormat;

	public DefaultPathResolver(HttpServletRequest request, AcceptHeaderToFormat acceptHeaderToFormat) {
		this.request = request;
		this.acceptHeaderToFormat = acceptHeaderToFormat;
	}

	public String pathFor(ResourceMethod method) {
		String acceptedHeader = request.getHeader("Accept");
		String format = "html"; 
		
		if (acceptedHeader != null)
			format = acceptHeaderToFormat.getFormat(acceptedHeader);
		
		String formatParam = request.getParameter("_format");
		if (formatParam != null)
			format = formatParam;
		
		String suffix = "";
		if (format != null && !format.equals("html")) {
			suffix = "." + format;
		}
        String name = method.getResource().getType().getSimpleName();
        String folderName = extractControllerFromName(name);
		return getPrefix() + folderName + "/" + method.getMethod().getName() + suffix
				+ "."+getExtension();
	}

	protected String getPrefix() {
		return "/WEB-INF/jsp/";
	}

	protected String getExtension() {
		return "jsp";
	}

    private String extractControllerFromName(String baseName) {
        baseName = lowerFirstCharacter(baseName);
        if (baseName.endsWith("Controller")) {
            return baseName.substring(0, baseName.lastIndexOf("Controller"));
        }
        return baseName;
    }

    private String lowerFirstCharacter(String baseName) {
        return baseName.toLowerCase().substring(0, 1) + baseName.substring(1, baseName.length());
    }
}
