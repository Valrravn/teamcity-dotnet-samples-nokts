#pragma checksum "/Users/Dmitrii.Korovin/Work/TC-DotNet-Samples/Clock.Web/Shared/MainLayout.razor" "{ff1816ec-aa5e-4d10-87f7-6f4963833460}" "78ace132d7abec3f2a97a454a6aef0632d517b7d"
// <auto-generated/>
#pragma warning disable 1591
namespace Clock.Web.Shared
{
    #line hidden
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Threading.Tasks;
    using Microsoft.AspNetCore.Components;
#nullable restore
#line 1 "/Users/Dmitrii.Korovin/Work/TC-DotNet-Samples/Clock.Web/_Imports.razor"
using System.Net.Http;

#line default
#line hidden
#nullable disable
#nullable restore
#line 2 "/Users/Dmitrii.Korovin/Work/TC-DotNet-Samples/Clock.Web/_Imports.razor"
using Microsoft.AspNetCore.Authorization;

#line default
#line hidden
#nullable disable
#nullable restore
#line 3 "/Users/Dmitrii.Korovin/Work/TC-DotNet-Samples/Clock.Web/_Imports.razor"
using Microsoft.AspNetCore.Components.Authorization;

#line default
#line hidden
#nullable disable
#nullable restore
#line 4 "/Users/Dmitrii.Korovin/Work/TC-DotNet-Samples/Clock.Web/_Imports.razor"
using Microsoft.AspNetCore.Components.Forms;

#line default
#line hidden
#nullable disable
#nullable restore
#line 5 "/Users/Dmitrii.Korovin/Work/TC-DotNet-Samples/Clock.Web/_Imports.razor"
using Microsoft.AspNetCore.Components.Routing;

#line default
#line hidden
#nullable disable
#nullable restore
#line 6 "/Users/Dmitrii.Korovin/Work/TC-DotNet-Samples/Clock.Web/_Imports.razor"
using Microsoft.AspNetCore.Components.Web;

#line default
#line hidden
#nullable disable
#nullable restore
#line 7 "/Users/Dmitrii.Korovin/Work/TC-DotNet-Samples/Clock.Web/_Imports.razor"
using Microsoft.JSInterop;

#line default
#line hidden
#nullable disable
#nullable restore
#line 8 "/Users/Dmitrii.Korovin/Work/TC-DotNet-Samples/Clock.Web/_Imports.razor"
using Clock.Web;

#line default
#line hidden
#nullable disable
#nullable restore
#line 9 "/Users/Dmitrii.Korovin/Work/TC-DotNet-Samples/Clock.Web/_Imports.razor"
using Clock.Web.Shared;

#line default
#line hidden
#nullable disable
    public partial class MainLayout : LayoutComponentBase
    {
        #pragma warning disable 1998
        protected override void BuildRenderTree(global::Microsoft.AspNetCore.Components.Rendering.RenderTreeBuilder __builder)
        {
            __builder.OpenElement(0, "div");
            __builder.AddAttribute(1, "class", "sidebar");
            __builder.OpenComponent<global::Clock.Web.Shared.NavMenu>(2);
            __builder.CloseComponent();
            __builder.CloseElement();
            __builder.AddMarkupContent(3, "\n\n");
            __builder.OpenElement(4, "div");
            __builder.AddAttribute(5, "class", "main");
            __builder.AddMarkupContent(6, "<div class=\"top-row px-4\"><a href=\"https://github.com/JetBrains/teamcity-dotnet-plugin/\" target=\"_blank\">About</a></div>\n\n    ");
            __builder.OpenElement(7, "div");
            __builder.AddAttribute(8, "class", "content px-4");
#nullable restore
#line 13 "/Users/Dmitrii.Korovin/Work/TC-DotNet-Samples/Clock.Web/Shared/MainLayout.razor"
__builder.AddContent(9, Body);

#line default
#line hidden
#nullable disable
            __builder.CloseElement();
            __builder.CloseElement();
        }
        #pragma warning restore 1998
    }
}
#pragma warning restore 1591
