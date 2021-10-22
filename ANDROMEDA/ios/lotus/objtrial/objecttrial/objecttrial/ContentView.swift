//
//  ContentView.swift
//  objecttrial
//
//  Created by TRIAL on 26/08/21.
//

import SwiftUI

struct ContentView: View {
    @available(iOS 13.0.0, *)
    var body: some View {
        
        VStack{
            Spacer()
            CustomTabs()
        }
    }
}
@available(iOS 13.0.0, *)
struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}

@available(iOS 13.0.0, *)
struct CustomTabs : View {
    var body: some View{
        HStack{
            Button(action: {
                
            }){
                 Image("iclts")
            }
            
            Button(action: {
                
            }){
                 Image("icpromo")
            }
            Button(action: {
                
            }){
                 Image("iccall")
            }
            Button(action: {
                
            }){
                 Image("icgear")
            }
        }
    }
}
