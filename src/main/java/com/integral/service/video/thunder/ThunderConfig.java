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

    public static String fp_raw = "TW96aWxsYS81LjAgKFdpbmRvd3MgTlQgNi4xOyBXT1c2NCkgQXBwbGVXZWJLaXQvNTM3LjM2IChLSFRNTCwgbGlrZSBHZWNrbykgQ2hyb21lLzU1LjAuMjg4My44NyBTYWZhcmkvNTM3LjM2IyMjemgtQ04jIyMyNCMjIzkwMHgxNDQwIyMjLTQ4MCMjI3RydWUjIyN0cnVlIyMjdHJ1ZSMjI3VuZGVmaW5lZCMjI2Z1bmN0aW9uIyMjIyMjV2luMzIjIyMjIyNXaWRldmluZSBDb250ZW50IERlY3J5cHRpb24gTW9kdWxlOjpFbmFibGVzIFdpZGV2aW5lIGxpY2Vuc2VzIGZvciBwbGF5YmFjayBvZiBIVE1MIGF1ZGlvL3ZpZGVvIGNvbnRlbnQuICh2ZXJzaW9uOiAxLjQuOC45MDMpOjphcHBsaWNhdGlvbi94LXBwYXBpLXdpZGV2aW5lLWNkbX47Q2hyb21lIFBERiBWaWV3ZXI6Ojo6YXBwbGljYXRpb24vcGRmfjtTaG9ja3dhdmUgRmxhc2g6OlNob2Nrd2F2ZSBGbGFzaCAyMy4wIHIwOjphcHBsaWNhdGlvbi94LXNob2Nrd2F2ZS1mbGFzaH5zd2YsYXBwbGljYXRpb24vZnV0dXJlc3BsYXNofnNwbDtOYXRpdmUgQ2xpZW50Ojo6OmFwcGxpY2F0aW9uL3gtbmFjbH4sYXBwbGljYXRpb24veC1wbmFjbH47Q2hyb21lIFBERiBWaWV3ZXI6OlBvcnRhYmxlIERvY3VtZW50IEZvcm1hdDo6YXBwbGljYXRpb24veC1nb29nbGUtY2hyb21lLXBkZn5wZGYjIyMyMmI0NjkyZjk4ZjBkN2E0OGVlMzc3Mjg5ODFjZDFjNQ==";
}
