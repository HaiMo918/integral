package com.integral.service.video.thunder;

/**
 * Created by liuqinghai on 2017/3/6.
 */
public class ThunderConfig {
    public String agent="Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36";
    public String language="zh-CN";
    public String colorDepth="24";
    public String screen_resolution="900x1440";
    public String timezoneOffset="-480";
    public boolean hasSessionStorage=true;
    public boolean hasLocalStorage=true;
    public boolean indexedDB=true;
    public String undefined="undefined";
    public String typeOfOpenDatabase="function";
    public String cpuClass="";
    public String platform="Win32";
    public String doNotTrack="";
    public String pluginString = "Widevine Content Decryption Module::Enables Widevine licenses for playback of HTML audio/video content. (version: 1.4.8.903)::application/x-ppapi-widevine-cdm~;Chrome PDF Viewer::::application/pdf~;Shockwave Flash::Shockwave Flash 23.0 r0::application/x-shockwave-flash~swf,application/futuresplash~spl;Native Client::::application/x-nacl~,application/x-pnacl~;Chrome PDF Viewer::Portable Document Format::application/x-google-chrome-pdf~pdf";
    public String canvasFingerprint="22b4692f98f0d7a48ee37728981cd1c5";

    @Override
    public String toString() {
        return agent+"###"+language+"###"+colorDepth+"###"+screen_resolution+"###"+timezoneOffset+"###"
                +hasSessionStorage+"###"+hasLocalStorage+"###"+indexedDB+"###"+undefined+"###"+typeOfOpenDatabase+"###"+cpuClass+"###"+platform+"###"+doNotTrack+"###"+pluginString+"###"+canvasFingerprint;
    }

    public static String fp_raw = "TW96aWxsYS81LjAgKFdpbmRvd3MgTlQgNi4xOyBXaW42NDsgeDY0KSBBcHBsZVdlYktpdC81MzcuMzYgKEtIVE1MLCBsaWtlIEdlY2tvKSBDaHJvbWUvNTQuMC4yODQwLjk5IFNhZmFyaS81MzcuMzYjIyN6aC1DTiMjIzI0IyMjMTA4MHgxOTIwIyMjLTQ4MCMjI3RydWUjIyN0cnVlIyMjdHJ1ZSMjI3VuZGVmaW5lZCMjI2Z1bmN0aW9uIyMjIyMjV2luMzIjIyMjIyNXaWRldmluZSBDb250ZW50IERlY3J5cHRpb24gTW9kdWxlOjpFbmFibGVzIFdpZGV2aW5lIGxpY2Vuc2VzIGZvciBwbGF5YmFjayBvZiBIVE1MIGF1ZGlvL3ZpZGVvIGNvbnRlbnQuICh2ZXJzaW9uOiAxLjQuOC45MDMpOjphcHBsaWNhdGlvbi94LXBwYXBpLXdpZGV2aW5lLWNkbX47U2hvY2t3YXZlIEZsYXNoOjpTaG9ja3dhdmUgRmxhc2ggMjQuMCByMDo6YXBwbGljYXRpb24veC1zaG9ja3dhdmUtZmxhc2h+c3dmLGFwcGxpY2F0aW9uL2Z1dHVyZXNwbGFzaH5zcGw7Q2hyb21lIFBERiBWaWV3ZXI6Ojo6YXBwbGljYXRpb24vcGRmfnBkZjtOYXRpdmUgQ2xpZW50Ojo6OmFwcGxpY2F0aW9uL3gtbmFjbH4sYXBwbGljYXRpb24veC1wbmFjbH47Q2hyb21lIFBERiBWaWV3ZXI6OlBvcnRhYmxlIERvY3VtZW50IEZvcm1hdDo6YXBwbGljYXRpb24veC1nb29nbGUtY2hyb21lLXBkZn5wZGYjIyMyMmI0NjkyZjk4ZjBkN2E0OGVlMzc3Mjg5ODFjZDFjNQ==";
}
