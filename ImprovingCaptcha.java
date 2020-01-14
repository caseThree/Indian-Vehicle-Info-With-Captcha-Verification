class ImprovingCaptcha{
    private String improve_J(String s){
        String modified="";
        for(int i=0;i<s.length();i++){
            if(s.charAt(i)==')')
                modified=modified+'J';
            else
                modified=modified+s.charAt(i);
        }
        return modified;
    }
    public String improve(String s){
        s=improve_J(s);
        //s=improve_0(s);
        s=s.toUpperCase();
        return s;
    }
}
