//
//  PriceVController.swift
//  ltsarc
//
//  Created by TRIAL on 28/08/21.
//

import UIKit
import Alamofire
import SwiftyJSON


class PriceVController: UIViewController {
   
    @IBOutlet weak var txby: UILabel!
    @IBOutlet weak var tblPrice: UITableView!
    var  prices = [dtPrice]()
    var Txbuyback = ""
    var nma = [String]()
    var wgh = [String]()
    var prc = [String]()
    var trial = ["q","2"]
   var Jml = 0
//    var wgh = [""]
//    var prs = [""]
    var Baseurl = "https://lotusarchi.com/api/products/info"
    var hargas = [Harga]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .red
        self.tabBarController?.tabBar.isHidden = true
       // title = "Price"
 
     //   hargas.append(tblist(name: "nama",weight: "weight",after_tax: "after"))
        
       // let service = Service(Baseurl: "https://lotusarchi.com/api/products/info")
        getList(url_: Baseurl,lbby: txby)
        tblPrice.dataSource = self
        tblPrice.register(UITableViewCell.self, forCellReuseIdentifier: "idxPrice")
//        service.getHarga()
//        service.completionhandler { [weak self](prices,status,message) in
//            if status {
//                guard let self = self else {return}
//                guard let _prices = prices else {return}
//                self.prices = _prices
//                self.tblPrice.reloadData()
//            }
//        }

   //     Alamofire.Request
        // Do any additional setup after loading the view.
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
                    var weight = dtjson["products"][i]["weight"]
                    let after = dtjson["products"][i]["after_tax"]
                 //   print(weight)
                    self.nma.append(nama.stringValue)
                    self.wgh.append(weight.stringValue)
                    self.prc.append(after.stringValue)
                  //  self.listHarga.append(Harga(nama,weight,after))
                   // print(nama)
                }
                self.Jml = self.nma.count
                self.tblPrice.reloadData()
            //    print(self.har.count)
                
              //  print(harga as Any)
                lbby.text="Buyback Price    Rp "+String(harga!)+",- per gram  Last Update :"+tglUpdate!
              
            }
            }
    }
    
    struct tblist {
        let name:String
        let weight:Double
        let after_tax:Int
    }
    var listHarga = [tblist]()
    
}
//
extension PriceVController: UITableViewDataSource{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.nma.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        var cell = tableView.dequeueReusableCell(withIdentifier: "idxPrice", for: indexPath)
        cell = UITableViewCell(style: .subtitle, reuseIdentifier: "idxPrice")
        
       let nama = nma[indexPath.row]
       let prcs = prc[indexPath.row]
                    if (indexPath.row % 2 == 0)
                    {
                        cell.backgroundColor = UIColor.lightGray
                    }
                    else
                    {
                        cell.backgroundColor = UIColor.white
                    }
        print(indexPath.row)
//      //  print("nama == \(String(describing: price.buyback_price))")
//            cell?.textLabel?.text =  nama
     //   cell.detailTextLabel?.text = prcs
        cell.textLabel?.font = UIFont.systemFont(ofSize: 15.0)
        cell.textLabel?.text = nama
       // cell.backgroundColor =
        cell.detailTextLabel?.text = "Rp \(prcs),-"
        return cell
    }


}

//extension PriceVController {
//
//  }
//}
