/*
 * Copyrighted 2012-2013 Netherlands eScience Center.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache License at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * For the full license, see: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 */
// source: 

package nl.esciencecenter.glite.lfc.internal;
// ====
// Piter T. de Boer: replaces with CnsLong Response
// === 

///*****************************************************************************
// * Copyright (c) 2007, 2007 g-Eclipse Consortium 
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the Eclipse Public License v1.0
// * which accompanies this distribution, and is available at
// * http://www.eclipse.org/legal/epl-v10.html
// *
// * Initial development of the original code was made for the
// * g-Eclipse project founded by European Union
// * project number: FP6-IST-034327  http://www.geclipse.eu/
// *
// * Contributors:
// *    Mateusz Pabis (PSNC) - initial API and implementation
// *****************************************************************************/
//package eu.geclipse.lfc.internal;
//
//import java.io.DataInputStream;
//import java.io.IOException;
//
//import eu.geclipse.lfc.LFCError;
//import eu.geclipse.lfc.LFCLogger;
//import eu.geclipse.lfc.Messages;
//import eu.geclipse.lfc.internal.AbstractCnsResponse;
//import eu.geclipse.lfc.internal.CnsConstants;
//
///**
// *  Encapsulates LFC server response to requested OPENDIR command.
// *  
// *  Receives 12 byte header and then directory id (as fileId)
// */
//public class CnsOpenDirResponse extends AbstractCnsResponse {
//
//  private long fileid = 0 ;
//
//  
//  /**
//   * @return opened directory id (as file id)
//   */
//  public long getFileid() {
//    return this.fileid;
//  }
//
//  @Override
//  public void readFrom( final DataInputStream input ) throws IOException {
//    LFCLogger.ioLogMessage( Messages.lfc_log_recv_opendir ); 
//    // Header
//    super.readFrom( input );
//    
//    // check for response type
//    if( this.type == CnsConstants.CNS_RC ) {
//      // received RESET CONTEXT request!
//      // we have an error!
//      LFCLogger.ioLogMessage( "RESPONSE: " + LFCError.getMessage( this.size ) ); //$NON-NLS-1$
//    } else {
//      // Data
//      this.fileid = input.readLong();
//      this.size = super.receiveHeader( input );
//    } 
//  }
//
//
//  
//  
//}
