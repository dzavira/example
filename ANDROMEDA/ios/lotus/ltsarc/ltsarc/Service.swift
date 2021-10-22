//
//  Service.swift
//  ltsarc
//
//  Created by TRIAL on 06/10/21.
//

import Foundation
import Alamofire
import SwiftyJSON

class Service {
    //https://lotusarchi.com/api/products/info
    fileprivate var BaseUrl=""
    typealias priceCallback = (_ prices:[dtPrice]?,_ status: Bool, _ message: String) -> Void
    var callBack: priceCallback?
    init(Baseurl: String) {
        self.BaseUrl = Baseurl
    }
    
    func getHarga(){
        AF.request(self.BaseUrl, method: .get, parameters: nil, encoding: URLEncoding.default, headers: nil, interceptor: nil).response {
            (responseData) in
          //  print("dapat")
            guard let data = responseData.data else {
                self.callBack?(nil, false, "")
                return}
            do{
            let prices = try JSONDecoder().decode([dtPrice].self, from: data)
                print("datanya ",prices)
                self.callBack?(prices,true,"")
            } catch {
                self.callBack?(nil, false, error.localizedDescription)
            }
        }
    }
    
    func completionhandler(callback: @escaping priceCallback){
        self.callBack = callback
    }
    
    func getList(url_: String, lbby: UILabel){
        AF.request(url_).responseJSON{
            (response) in
            switch response.result{
            case .failure:
                print("fail")
            case .success(let value):
                let dtjson = JSON(value)
                
              //  print(response)
                let tglUpdate = dtjson["last_update"]["fulldate"].string
                let harga = dtjson[]["buyback_price"].int
                
                for i in 0 ..< dtjson["products"].count{
                    let nama = dtjson["products"][i]["name"]
                    print(nama)
                }
               //
              //  print(harga as Any)
                lbby.text="Buyback Price Rp"+String(harga!)+",- per gram   Last Update :"+tglUpdate!
              
            }
            }
    }
    
}
