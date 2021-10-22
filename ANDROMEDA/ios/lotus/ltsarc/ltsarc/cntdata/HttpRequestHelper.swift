//
//  HttpRequestHelper.swift
//  ltsarc
//
//  Created by TRIAL on 07/10/21.
//

import Foundation

class HttpRequestHelper {
    func GET(url: String, complete: @escaping (Bool, Data?) -> ()) {
        var request = URLRequest(url: URL(string: url)!)
        request.httpMethod = "GET"
        URLSession.shared.dataTask(with: request) { data, response, error in
            guard error == nil else {
                print("Error: problem calling GET")
                print(error!)
                complete(false, nil)
                return
            }
            guard let data = data else {
                print("Error: did not receive data")
                complete(false, nil)
                return
            }
            guard let response = response as? HTTPURLResponse, (200 ..< 300) ~= response.statusCode else {
                print("Error: HTTP request failed")
                complete(false, nil)
                return
            }
            complete(true, data)
        }.resume()
    }
}
